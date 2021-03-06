# Changelog

## 0.8.13
* Fix possibility of granting permission to read phone state or external storage. This bug was introduced by Google and has been resolved.
* Minor text fixes

## 0.8.12
* Add custom cluster and cluster item icons
* Add floating action button tooltip on devices running Android Oreo and above
* Add option to toggle exact cluster event count
* Add event settings. You are now able to chose your desired measurement units.
* Add notifications about event reload on event count change and finished load
* Events will be loaded in the intro, therefore you are able to view events once you finished the intro.
* Change toolbar title based on the current view
* Update icons
* Improve (optional) location tracking
* Zoom in on clicking my location button
* Detail view is now full screen
* Style map in details view according to user settings
* Style details view according to user settings
* Remove map camera bounds targets. You are now able to freely explore the whole map.
* Location permission is now optional. You can enjoy Aardvark without exposing you location. To enable extended functionality, such as viewing your current location and seeing the distance to events, please grant the location permission either via location settings or by pressing the floating action button in the map view. Don't worry about your privacy, your location is never shared or saved somewhere.
* Update dependencies
* Disable automatic zoom to all markers when rotating your screen in map view
* Replace most snackbars with toast. Android 8.1 emphasizes the intent of toasts over snackbars without custom actions.
* Fix floating action button visibility in certain tab switching scenarios
* Fix replaying the intro
* Fix intro bar position selection
* Fix viewpager cutoff in main view
* Fix UNIX time conversion. Dates and time should be correctly displayed until 03:14:07 UTC on 19 January 2038.
* Minor bugfixes and performance improvements

## 0.8.11
* Add map settings
* Fix event detail initialization
* Fix issues with cutoff view pager when using the bottom layout
* Fix security issues for WiFI-ADB (experimental)

## 0.8.10
* Minor bug fixes

## 0.8.9
* Improve tab reselection. Each tabs has its own action. The event tab reloads events and the map tab moves the camera to include all event markers.
* Minor fixes and performance improvements

## 0.8.8
* Add reload animation on tab reselect
* Add tooltip to floating action buttons
* Add setting to change date/time format
* Add custom my location button to map
* Improve animations
* Improve map icons
* Remove obsolete map types
* Fix action bar popup theme
* Fix out of sync events for map. This fixes the empty map on first start.

## 0.8.7
* Add visual loading indicator when sorting events
* Improve performance
* Improve location tracking
* Improve animations
* Improve settings arrangement and design
* Improve location permission management
* Improve fingerprint authentication UI (experimental)
* Fix measurement units

## 0.8.6
* Add fab to move to top of events list
* Add activity recognition
* Add reverse geocoding
* Improve location detection
* Bar layout can now be changed by clicking the whole screen in intro
* Reloading events removes all old events
* Update dependencies

## 0.8.5
* Add location permission prompt in intro
* Fix double changelog on first start
* Change event row design to cards

## 0.8.4
* Adapt to new data structure
* Replace bottom navigation with top and bottom tab layout
* Improve header theme design
* Improve event sorting performance
* Minor fixes and performance improvements

## 0.8.3
* Minor bug fixes and performance improvements
* Add performance analytics
* Add main activity layout toggle in intro
* Update dependencies

## 0.8.2
* Add custom map styles
* Add setting to toggle viewpager slide navigation

## 0.8.1
* Improve navigations tabs

## 0.8.0
* Save events in Room database
* Animate changes to events
* Reload events when a currently selected tab is reselected
* Remove navigation drawer
* Tidy up intro
* Properly theme event details
* Reimplement location tracking
* Update icons
* Improve event marker clustering performance
* Bottom bar is the new default
* Improve location permission handling
* Implement full backup
* Add option to enable permissions in settings
* Replace all toasts with a snackbar
* Update dependencies

## 0.7.0
* Complete overhaul
* Fully themeable design
* Add badged icons to tabs. This allows info about the number of events, close events and so on.
* Add navigation drawer for easier user switching. This might be removed if there is no demand for user switching. Also currently the navigation drawer only contains the same items, which are available with the tabs.
* Add map styling options
* Add zoom to all markers menu item for map
* Add a lot more settings. Just look for yourself.
* Replace bottom navigation with tabs. These can be moved from the bottom to the top.
* Disable map movement scroll gestures
* Only show the map in event detail view, if the event was gathered from the event list.
* Fix location icon misalignment with different distances
* Probably a lot more i forgot since i started this refactor about two months ago.

## 0.6.7
* Add preference for secure flag (prohibits screenshots)
* Add credential check for predefined user and password
* Update libraries

## 0.6.6
* Re-add DiffUtil (WIP)
* Add time to detail view
* Disable click functionality on detail view map
* Add fast scroller

## 0.6.5
* Secure app from screenshots and other interception techniques
* Overhaul event detail view
* Fix landscape bottom navigation background color
* Improve splash screen
* Replace skip button with back button in intro
* Improve color scheme
* Remove maximum characters for username and password
* Update dependencies
* Update SDK version to 27

## 0.6.4
* Changelog overhaul
* Minor bug fixes

## 0.6.3
* Fix event sorting
* Fix fingerprint unlock after device sleep

## 0.6.2
* Update login flow
* Properly handle fingerprint authentication after app resume

## 0.6.1
* Update login flow
* Add fingerprint authentication after login
* Add more german translations
* Add adaptive icons (Android Oreo required)
* Add option to change Serval credentials
* Add opt-out analytics option (#neverSettle)
* Properly check location permission and update UI accordingly
* Fix clicking through dashboard
* Fix event sorting

## 0.6.0
* Utilize Kerval API to load events from Serval
* Overhaul settings
* Add german translations
* Improve intro with splash screen (on event loading)

## 0.5.1
* Performance improvements
* Fix menu visiblity

## 0.5.0
* Add the ability to load events via swipe to refresh in events overview
* Add new intro
* Performance improvements

## 0.4.12
* Serval project is now Aardvark
* Add new icons

## 0.4.11
* Minor bugfixes

## 0.4.10
* Minor bugfixes

## 0.4.9
* Improve map performance for event details
* Optimize map padding on event cluster click
* Clicking the fab in event details now searches the event in Google Maps instead of directly navigating to it
* Use placeholder layout for dashboard until the design is completed

## 0.4.8
* Unify event detail layout

## 0.4.7
* Improve settings
* Improve changelog display
* Add events sorting options (measurements, shuffle)
* Add ability to show changelog
* Add touch ripple feedback to events overview
* Update app icon
* Remove fastscroller for events overview

## 0.4.6
* Add list of measurements in event details

## 0.4.5
* Add map style selection

## 0.4.4
* Add event clustering in map

## 0.4.3
* Optimize event information layout for landscape orientation

## 0.4.2
* Fix device rotation issues

## 0.4.1
* Add event sorting by distance, time or measurement type count
* Improve map performance
* Fix event time for events older than a week

## 0.4.0
* Events are now loaded asynchronously
* Properly respect location permission
* Fix device rotation issues

## 0.3.0
* Event loading changes

## 0.2.0
* Add analytics

## 0.1.0
* Complete overhaul of maps
* Add placeholder fragment
* Add ability to delete events by swiping
* Show formatted distance and time of events
* Change secondary color to a more vivid and warm blue
* Fix clipping of views under bottom navigation

## 0.0.7
* Measurements can now have two additional types (wind, precipitation)
* Change color scheme
* Fix measurement icon display
* Add fastscroller to events view
* Complete overhaul of intro

## 0.0.6
* Complete UI overhaul
* Dashboard isn't implemented yet
* Events shows the last occurred events
* Map shows last occurred events in close proximity
* Add icon for events

## 0.0.5
* Add icon for temperature measurement type
* Show icon corresponding to events measurement type with most measurements
* Disable toolbar on map

## 0.0.4
* Add changelog on version update
* Disable all gestures on map

## 0.0.3
* Add the ability to navigate to event locations
* Change fab icon to navigation icon in event details

## 0.0.2
* Add Google Play Services Maps
* Add map to event information
* Remove ACCESS_COARSE_LOCATION permission

## 0.0.1
* Initial release
