plugins {
    id("java")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "me.pulse.launcher.Launcher"
        }
    }
}