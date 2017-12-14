# Oxygen Dita Prolog Updater
Updates the author information, created and modified dates in the DITA prolog section for topics and maps.


## Installation
This add-on is compatible with Oxygen XML Editor (or XML Author) version 19.0 or higher. 

You can install the add-on by using Oxygen's add-ons support. In Oxygen, go to Help->Install new add-ons... and use this add-on repository URL:

https://raw.githubusercontent.com/oxygenxml/dita-prolog-updater/master/build/addon.xml

then continue the installation process.

## Configuration
To configure the update options, open the Preferences dialog box (Options->Preferences) and go to  Plugins-> Oxygen Dita Prolog Updater.

The following options are available:

- **Author**: Set the author name. By default, it's the system user name.
- **Enable automatic prolog update on save**: When this option is selected, the prolog is updated in the document.
- **Set the creator name**: When it's enable and selected, an author with type's value creator is setted on save when the document is new.
- **Set the created date**: When it's enable and selected, the created date is setted when the document is new.
- **Update the contributor names**: When it's enable and selected, the document is updated with an author with type's value contributor.
- **Update the revised dates**:  When it's enable and selected, the document is updated with a revised element. 
### Quality Checks
https://sonarcloud.io/dashboard?id=oxygen-dita-prolog-updater%3Aoxygen-dita-prolog-updater
