# CharTrackerAndroid
CharTracker is an android app developed using Kotlin, Jetpack Compose, Google Firebase, Google AdMob. The app is desgined to allow users to log information about characters in stories to help users to keep track of who's who across the different stories they are reading/watching/playing through. This is especially helpful when jumping back in after a break (whether from busyness, waiting for the next release, etc.) and aims to make that experience less daunting. Users are able to fill in preselected text fields as well as upload images for characters and stories. The information is divided up by stories which contain characters and factions. 

The app is a solo developer project so advice and feedback is welcome.

# Tools
- To authenticate users Google Firebase Authentication was implemented with usernames, passwords, and email verification.
- To store user text data Google Firebase Firestore was implemented. 
- To store user images Google Firebase Storage was implemented.
- To display images [Glide](https://github.com/bumptech/glide) was implemented.
- To allow users to crop the images they upload [Android-Image-Cropper](https://github.com/CanHub/Android-Image-Cropper) was implemented.
- To compress user uploaded images to prevent slow and expensive uploads [Compressor](https://github.com/zetbaitsu/Compressor) was implemented.
- To allow users to pick colors for the factions [colorpicker-compose](https://github.com/skydoves/colorpicker-compose) was implemented.
- To provide logging [timber](https://github.com/JakeWharton/timber) was utilized.

# Screens
These are some of the key screens of the app.

| Sign In     | Stories      | Adding a Story | Factions (with dialog) |
| ------------- | ------------- | ------------- | ------------- |
| ![SignIn](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/128c06e6-1d05-4d97-946c-74e439285135) | ![Stories](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/f222dc42-201c-4fe4-830f-c232c5279b9f) |  ![AddStory](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/b3455017-3143-4f97-8c98-fd8e9a3bc736) | ![Factions](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/21cf11f1-6674-4061-b542-85ba16858862) |




| Characters     | Adding a Character      | Editing a Character | Character Details |
| ------------- | ------------- | ------------- |------------- |
| ![Characters](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/6387c136-3d6f-4421-8ad9-3bb31c1a8bbf) | ![AddCharacter1](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/326ff79d-3a4e-492d-8ab1-c3ff3ce50791) | ![EditCharacter1](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/d64aab7b-a2d4-48a1-ba41-d958a941be9b) | ![CharacterDetails](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/65310f3c-8d75-4226-b5fb-21bbef24a86a) |
| | ![Character2](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/59109de9-9368-415f-884d-84ce91b6ff5a) | ![EditCharacter2](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/1c2b2e1f-f9fc-4b35-bc72-da7b0294d627) | |

| Settings     |
| ------------- | 
| ![Settings](https://github.com/rhtakaha/CharTrackerAndroid/assets/55594263/d572aea9-41e0-4b72-a30d-e6359f67b6d2) |
