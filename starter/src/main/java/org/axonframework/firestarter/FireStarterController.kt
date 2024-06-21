package org.axonframework.firestarter

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.ResourceUtils
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.*
import java.nio.charset.Charset

@Controller
@RequestMapping("fire-starter")
class FireStarterController(
    private val settingsHolder: FireStarterSettingsHolder,
) {
    @GetMapping(value = ["/"], produces = ["text/html"])
    @ResponseBody
    fun serveFrontend(): String {
        val stream = ResourceUtils.getURL("classpath:static/fire-starter/index.html").openStream()
        return StreamUtils.copyToString(stream, Charset.defaultCharset())
    }

    @PostMapping("settings")
    fun setConfig(@RequestBody settings: FireStarterSettings): ResponseEntity<String> {
        settingsHolder.setSettings(settings)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("settings", produces = ["application/json"])
    fun getConfig(): ResponseEntity<FireStarterSettings> {
        return ResponseEntity.ok(settingsHolder.getSettings())
    }
}
