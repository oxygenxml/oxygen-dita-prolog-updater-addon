# Oxygen DITA Prolog Updater
Oxygen XML plugin that contributes a preferences page that includes various options for updating the "prolog" section of a DITA topic/map.

## Compatibility
This add-on is compatible with Oxygen XML Editor (or XML Author) version 19.0 or higher. 

## How to install

1. In Oxygen, go to **Help->Install new add-ons** to open an add-on selection dialog box.
2. Enter or paste https://raw.githubusercontent.com/oxygenxml/dita-prolog-updater/master/build/addon.xml in the **Show add-ons from** field.
3. Select the **DITA Prolog Updater** add-on and click Next.
4. Read the end-user license agreement. Then select the **I accept all terms of the end-user license agreement** option and click **Finish**.
5. Restart the application.

Result: A **DITA Prolog Updater** preferences page will now be available in **Options->Preferences->Plugins**. This preferences page includes various options for updating the prolog section of a DITA topic or map.

## Configuration
To configure the update options, go to **Options->Preferences->Plugins->DITA Prolog Updater**.

The following options can be set for DITA topics or maps (or both):

- **Author**: Specifies the name of the author. By default, it is the system user name.
- **Date format**: Specifies the format for the date added in the *created date* or *revised date* elements. If the specified format is invalid, *yyyy/MM/dd* is used as a fallback.
- **Limit the number of revised dates to**: Specifies the number of revisions that will be kept. Anytime a *revised* element is added in the prolog section and the specified limit has been reached, the oldest *revised* element is deleted.
- **Enable automatic prolog update on save**: When this option is selected, the prolog is updated anytime the document is saved.
- **Set creator name**: When this option is selected, the author is set as the document's *creator* in the prolog when the document is saved. This option is only applicable for new documents.
- **Set created date**: When this option is selected, a *created date* is added to the prolog when the document is saved. This option is only applicable for new documents.
- **Update contributor names**: When this option is selected, the author is set as a *contributor* in the prolog when the document is saved. This option is only applicable for already existing documents.
- **Update revised dates**: When this option is selected, a *revised date* is added to the prolog when the document is saved. This option is only applicable for already existing documents. 

Copyright and License
---------------------
Copyright 2018 Syncro Soft SRL.

This project is licensed under [Apache License 2.0](https://github.com/oxygenxml/oxygen-dita-prolog-updater/blob/master/LICENSE)
