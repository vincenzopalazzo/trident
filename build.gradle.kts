plugins {
    java
    application
}

repositories {
    jcenter()
}

dependencies {

    //implementation("io.github.vincenzopalazzo:material-ui-swing:1.1.1_pre-release_6.1")
    implementation(files("$projectDir/devlib/material-ui-swing-1.1.1-rc2.jar"))
    implementation(files("$projectDir/devlib/LinkLabelUI.jar"))
    implementation(files("$projectDir/devlib/SwingSnackBar-0.0.1.jar"))

    // https://mvnrepository.com/artifact/com.fifesoft/rsyntaxtextarea
    // https://github.com/bobbylight/RSyntaxTextArea
    implementation("com.fifesoft:rsyntaxtextarea:3.1.1")

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("ch.qos.logback:logback-core:1.2.3")
    implementation("org.slf4j:slf4j-api:1.7.25")

    testImplementation("junit:junit:4.12")
}

application {
    mainClassName = "org.trident.App"
}
