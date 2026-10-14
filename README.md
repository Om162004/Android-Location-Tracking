# Android Location Tracking

An Android application developed using Java to capture, store, and display GPS location data with scheduled background tracking.

## Features
- Capture GPS location updates, including latitude and longitude.
- Store location history locally using Room Database.
- Run location tracking through an Android Foreground Service.
- Schedule tracking using AlarmManager.
- Handle scheduled events using BroadcastReceiver.
- Display saved location history using RecyclerView.

## Technologies Used
- **Language:** Java
- **UI:** XML, RecyclerView
- **Database:** Room Database
- **Android Components:** Foreground Service, BroadcastReceiver, AlarmManager
- **Tools:** Android Studio, Git, GitHub

## Project Structure
- `adapter/` – Location list adapter
- `dao/` – Room database operations
- `database/` – Database configuration
- `model/` – Location entity
- `receiver/` – Scheduled event receiver
- `services/` – Location tracking service
- `utils/` – Location and scheduling helpers

## Status
Core implementation available. Refer to the source code for current functionality.

## Author
Omkaran Prajapati
