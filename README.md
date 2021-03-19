# Oxygen DITA Prolog Updater
Oxygen XML plugin that contributes a preferences page that includes various options for updating the "prolog" section of a DITA topic/map.

## Compatibility
This add-on is compatible with Oxygen XML Editor (or XML Author) version 19.0 or higher. 

## How to install

1. In Oxygen, go to **Help->Install new add-ons** to open an add-on selection dialog box.
2. Enter or paste https://www.oxygenxml.com/InstData/Addons/default/updateSite.xml in the **Show add-ons from** field.
3. Select the **DITA Prolog Updater** add-on and click Next.
4. Read the end-user license agreement. Then select the **I accept all terms of the end-user license agreement** option and click **Finish**.
5. Restart the application.

Result: A **DITA Prolog Updater** preferences page will now be available in **Options->Preferences->Plugins**. This preferences page includes various options for updating the prolog section of a DITA topic or map.

The add-on can also be installed using the following alternative installation procedure:
1. Go to the [Releases page](https://github.com/oxygenxml/oxygen-dita-prolog-updater/releases/latest) and download the `oxygen-dita-prolog-updater-{version}-plugin.jar` file.
2. Unzip it inside `{oXygenInstallDir}/plugins`. Make sure you don't create any intermediate folders. After unzipping the archive, the file system should look like this: `{oXygenInstallDir}/plugins/oxygen-dita-prolog-updater-x.y.z`, and inside this folder, there should be a `plugin.xml`file.


## Configuration
To configure the update options, go to **Options->Preferences->Plugins->DITA Prolog Updater**.

The following options can be set for DITA topics or maps (or both):

- **Author**: Specifies the name of the author. By default, it is the system user name.
- **Date format**: Specifies the format for the date added in the *created date* or *revised date* elements. If the specified format is invalid, *yyyy/MM/dd* is used as a fallback.
- **Limit the number of revised dates to**: Specifies the number of revisions that will be kept. Anytime a *revised* element is added in the prolog section and the specified limit has been reached, the oldest *revised* element is deleted.
- **Custom type attribute value for the original author**: Specifies a custom value for the *type* attribute of the *author* element that is inserted to specify the primary or original author of the content. When is not set, the *creator* value is used.
- **Custom type attribute value for an additional author**: Specifies a custom value for the *type* attribute of the *author* element that is inserted to specify an additional author who is not the primary/original author. When is not set, the *contributor* value is used.
- **Enable automatic prolog update on save**: When this option is selected, the prolog is updated anytime the document is saved.
- **Set creator name**: When this option is selected, the author is set as the document's *creator* in the prolog when the document is saved. This option is only applicable for new documents.
- **Set created date**: When this option is selected, a *created date* is added to the prolog when the document is saved. This option is only applicable for new documents.
- **Update contributor names**: When this option is selected, the author is set as a *contributor* in the prolog when the document is saved. This option is only applicable for already existing documents.
- **Update revised dates**: When this option is selected, a *revised date* is added to the prolog when the document is saved. This option is only applicable for already existing documents. 

Copyright and License
---------------------
Copyright 2018 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/oxygen-dita-prolog-updater/blob/master/LICENSE)
