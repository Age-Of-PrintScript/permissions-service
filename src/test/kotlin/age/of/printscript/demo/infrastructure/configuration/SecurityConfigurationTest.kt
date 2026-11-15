package age.of.printscript.demo.infrastructure.configuration

import age.of.printscript.demo.application.ManageSnippetMembersService
import age.of.printscript.demo.application.ShareResult
import age.of.printscript.demo.application.query.GetUserPermissionsUseCase
import age.of.printscript.demo.infrastructure.controller.PermissionController
import age.of.printscript.demo.infrastructure.controller.SnippetMembersController
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.security.oauth2.jwt.BadJwtException
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

@WebMvcTest(
    controllers = [PermissionController::class, SnippetMembersController::class],
    properties = [
        "AUTH0_DOMAIN=test.auth0.local",
        "AUTH0_AUDIENCE=https://test-audience",
    ],
)
@Import(SecurityConfiguration::class)
class SecurityConfigurationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    // Reemplaza al decoder real, que necesitaría contactar a Auth0 para obtener el JWKS.
    @MockitoBean
    private lateinit var jwtDecoder: JwtDecoder

    @MockitoBean
    private lateinit var getUserPermissions: GetUserPermissionsUseCase

    @MockitoBean
    private lateinit var manageMembers: ManageSnippetMembersService

    @Test
    fun `GET permissions without token returns 401`() {
        mockMvc
            .perform(get("/permissions").param("userId", "u").param("snippetId", "s"))
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET permissions with an invalid bearer token returns 401`() {
        `when`(jwtDecoder.decode("garbage")).thenThrow(BadJwtException("bad"))

        mockMvc
            .perform(
                get("/permissions")
                    .param("userId", "u")
                    .param("snippetId", "s")
                    .header("Authorization", "Bearer garbage"),
            ).andExpect(status().isUnauthorized)
    }

    @Test
    fun `GET permissions with a valid jwt returns 200`() {
        `when`(getUserPermissions("u", "s")).thenReturn(emptyList())

        mockMvc
            .perform(get("/permissions").param("userId", "u").param("snippetId", "s").with(jwt()))
            .andExpect(status().isOk)
    }

    @Test
    fun `POST members without token returns 401 and does not reach the service`() {
        mockMvc
            .perform(
                post("/snippets/s1/members")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"userId":"u2","roleId":"viewer"}"""),
            ).andExpect(status().isUnauthorized)

        verifyNoInteractions(manageMembers)
    }

    @Test
    fun `POST members uses the jwt subject as the requester, ignoring any X-User-Id header`() {
        `when`(manageMembers.share("auth0|owner", "u2", "s1", "viewer")).thenReturn(ShareResult.Success)

        mockMvc
            .perform(
                post("/snippets/s1/members")
                    .with(jwt().jwt { it.subject("auth0|owner") })
                    .header("X-User-Id", "someone-else")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("""{"userId":"u2","roleId":"viewer"}"""),
            ).andExpect(status().isCreated)

        verify(manageMembers).share("auth0|owner", "u2", "s1", "viewer")
    }
}
