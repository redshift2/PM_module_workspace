package workspace

import grails.plugins.Plugin

class WorkspaceGrailsPlugin extends Plugin {

    def grailsVersion = "7.0.0 > *"

    def title = "Workspace Management"
    def author = "Parts Manager"
    def description = "PLM Workspace Management"

    def profiles = ['web']
    def documentation = ""

    Closure doWithSpring() {
        { ->
        }
    }

    void doWithApplicationContext() {
    }
}
