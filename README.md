
## Project Overview
    This project is a simple native Android app that enables users to search for product information by 
    scanning barcodes or using keyword searches.

## Purpose and Goals
    The primary features of this project include:
    a. Barcode Scanning: Users can access the scanning interface via the scan button at the top of 
        the main screen. The interface includes a viewfinder, scanning animation, flashlight controls, and 
        other features. Upon successful scanning, the backend will query the database and display the product 
        information corresponding to the barcode. If no matching product is found, a notification will be displayed.
    b. Search Functionality: Users can search for keywords using the search bar on the main screen. 
        Upon clicking the search button, the app navigates to a search results page. If no results are found, 
        the interface displays a notification. The search page also supports "load more" functionality when 
        pulling down the list and allows navigation to a detailed view by clicking on a specific item.

## Programming Languages, Frameworks, Libraries, and Tools
    Tech Stack: Kotlin / Jetpack / Jetpack Compose
    Libraries Used: CameraX / MLKit / Coil / DI
    Tools: Postman / App Inspection

## Installation and Execution
    Target SDK Level: 34
    Minimum Supported SDK: 24 (Android Nougat, 7.0)

## Code Structure (MVVM)
    app/
    │
    ├── di/          # Dependency Injection
    │
    ├── data/
    |   ├── local/   # Entity
    │   ├── repositories/  # Data Access
    │   ├── remote/   # ApiService
    │   ├── utils/    # DataStore Utils
    │
    ├── ui/          # View & Screen & Theme
    │   ├── screen/
    │   ├── theme/
    │
    ├── viewmodel/   # SearchViewModel, ProductDetailViewModel, MainViewModel

##  Key Classes, Functions, or Components
    1. ApiService Interface: Implements network requests using Retrofit and relevant annotations.
    2. AppModule: Provides global singleton injections, such as for network services.
    3. ViewModel Files: Handle network data interfaces.

## Implementation of Key Features
    1. Scanning Functionality: Utilizes the CameraX library to open the camera preview interface, with a 
       viewfinder overlay and scanning animation. Before using the camera, it is necessary to check whether 
       the appropriate camera permissions have been granted.
    2. Barcode Processing: Uses MLKit’s BarcodeScanning feature to process images and identify barcode 
       information, followed by backend network queries.
    3. Search Functionality: Implements a search bar using Jetpack Compose’s SearchBar component, featuring
       search history and network-query-based results.
    4. Local Data Storage: Uses the DataStore library to store retrieved user IDs, enabling quick access 
       without repeated network requests.
    5. Dependency Injection: Ensures separation of data and UI, making the application easier to test and maintain.

## Known Issues and Limitations
    1. Subscription Key Expiration: The subscription key must be verified for validity before use. 
    2. Incomplete Search History: The search history feature requires further improvement.

## Improvement Directions
    1. Cache identical search results locally to reduce server load and enhance app performance. 
    2. Enhance the design of UI notifications. 
    3. Standardize UI layout parameters for consistency. 
    4. Centralize management of prompt text to simplify translation.