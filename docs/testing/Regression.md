# Regression Testing Notes
This page summarizes the scenarios to leverage when running regression tests on the Simple Content Portlet (SCP).

SCP supports 4 `names` or `types`:
* attachments
* attachments-manager
* web-component
* cms

Scenarios:
* `attachments` type
  * Not sure how to test it. A vanilla portlet definition yields a no-content portlet.
* `attachments-manager` type
  * Upload a file
  * View the file via the SCP link
  * View the preview and details
* `web-component` type
  * Edit HTML content - basic text area available
  * Displays the context as html.
* `cms` type
  * Can use the example ‘Snappy’ portlet in uP-start/data/quickstart
  * Portlet loads on page
  * No-op configure > save flow
  * Change HTML body and save
  * Upload image file, adding link to HTML body and save
  * Preview flow
  * Deprecated flows
    * Upload flash file - Deprecated, so we are not testing
    * Adding link to HTML body and save - Deprecated, so we are not testing
* General scenarios
    * Export portlet - should work for any portlet type
```shell
~$ ./gradlew :overlays:uportal:dataExport -Dtype=portlet-definition -Dsysid=snappy
```
  * Import portlet - should work for any portlet type
```shell
~$ ./gradlew overlays:uportal:dataImport -Ddir=path/to/the/new/or/updated/data/files/
```
* Other
  * Search
    * There is a search controller and ‘event-definitions’ in the portlet.xml.
    * While emptying caches doesn’t update the portlet’s search terms, an overriding dataImport picks up the search terms - however, it doesn’t exercise the controller.
  * Attachment persistence - for any of the specific scenarios that leverage attachments (cms, attachment, attachment-manager):
    * Database (default)
    * File
    * S3
