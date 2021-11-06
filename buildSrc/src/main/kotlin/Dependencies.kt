object ProjectVersions {
    const val openosrsVersion = "4.15.1"
    const val apiVersion = "^1.0.0"
}

object Libraries {
    private object Versions {
        const val guice = "4.2.3"
        const val javax = "1.3.2"
        const val lombok = "1.18.20"
        const val pf4j = "3.6.0"
        const val slf4j = "1.7.32"
    }

    const val guice = "com.google.inject:guice:${Versions.guice}"
    const val javax = "javax.annotation:javax.annotation-api:${Versions.javax}"
    const val lombok = "org.projectlombok:lombok:${Versions.lombok}"
    const val pf4j = "org.pf4j:pf4j:${Versions.pf4j}"
    const val slf4j = "org.slf4j:slf4j-api:${Versions.slf4j}"

}
