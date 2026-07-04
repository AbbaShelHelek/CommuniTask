# CommuniTask

CommuniTask is a planned Java/XML Android application for a community task board. Authenticated community members will be able to browse tasks, create and manage their own tasks, and maintain a profile.

## Planned Features

- Firebase Authentication registration, login, session routing, and logout.
- Firestore-backed user profiles and community task records.
- Task feed for all open community tasks.
- My Tasks view for tasks created by the signed-in user.
- Task details, create/edit flow, and owner-only deletion with confirmation.
- Profile screen for signed-in user information.

## Planned Technical Direction

- Java and XML layouts.
- Single `MainActivity` with Fragment-based screens.
- ViewBinding for view access; no `findViewById`.
- AndroidX Navigation Component for routing.
- RecyclerView for task lists.
- ViewModel and LiveData for lifecycle-aware UI state.
- Repository boundary for Firebase Authentication and Firestore access.
- Material Components for UI widgets and dialogs.

## Current Foundation Status

This branch contains the empty Android foundation only. Navigation graphs, Fragments, task models, Firebase repositories, adapters, dialogs, and full screens are intentionally left for later feature branches.

Firebase configuration requires a real `app/google-services.json` generated for the project application ID before Firebase runtime setup is added.
