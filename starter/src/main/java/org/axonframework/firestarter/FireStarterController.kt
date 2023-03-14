package org.axonframework.firestarter

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.util.ResourceUtils
import org.springframework.util.StreamUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import java.nio.charset.Charset

@Controller
@RequestMapping("fire-starter")
class FireStarterController {
    @GetMapping(value = ["/"], produces = ["text/html"])
    @ResponseBody
    fun serveFrontend(): String {
        val stream = ResourceUtils.getURL("classpath:static/fire-starter/index.html").openStream()
        return StreamUtils.copyToString(stream, Charset.defaultCharset())
    }

    @PostMapping("settings")
    fun setConfig(@RequestBody settings: FireStarterSettings): ResponseEntity<String> {
        FireStarterSettingsHolder.setSettings(settings)
        return ResponseEntity.accepted().build()
    }

    @GetMapping("settings", produces = ["application/json"])
    fun getConfig(): ResponseEntity<FireStarterSettings> {
        return ResponseEntity.ok(FireStarterSettingsHolder.getSettings())
    }
}
