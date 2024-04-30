/**
 * Import function triggers from their respective submodules:
 *
 * const {onCall} = require("firebase-functions/v2/https");
 * const {onDocumentWritten} = require("firebase-functions/v2/firestore");
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */

// The Cloud Functions for Firebase SDK to create Cloud Functions and triggers.
const {onRequest} = require("firebase-functions/v2/https");

// The Firebase Admin SDK to access Firestore.
const {initializeApp} = require("firebase-admin/app");
const {getFirestore} = require("firebase-admin/firestore");

const { getStorage } = require('firebase-admin/storage');

initializeApp({
    storageBucket: 'chartrackerandroid.appspot.com'
});

// cloud function that can be called to clean out Storage from any orphaned files
exports.maintainStorage = onRequest(async (req, res) => {
    // read from Firestore the list of all the files we know about
    const filesDoc = await getFirestore()
        .collection("all")
        .doc("imageFiles")
        .get();

    if (!filesDoc) {
        res.send("Failed to get the list of files!");
        return
    }
    const originalFileNames = filesDoc.get("imageFilenames");
    const fileNames = filesDoc.get("imageFilenames");

    // read all the file names from storage
    const bucket = getStorage().bucket();

    bucket.getFiles(
    async function(err, files) {
        if (!err) {
            var deletedFromStorage = false;
            var deletedFromList = false;
          // files is an array of File objects.
          files.forEach(element => {
                if (fileNames.includes(element.name)) {
                    // means that the file we found is known about so good!
                    // removing that from our list so we can remove any unnecessary list elements
                    fileNames.splice(fileNames.indexOf(element.name), 1);
                }else{
                    //means there is a file in storage not on the list then delete it (from storage)
                    bucket.file(element.name).delete();
                    deletedFromStorage = true;
                }
            });

            // if there is a file on the list and not in the storage delete it (from the doc list)
            const updatedFileNames = originalFileNames.filter(n => !fileNames.includes(n));

            if (updatedFileNames.length == originalFileNames.length && updatedFileNames.every((v) => originalFileNames.indexOf(v) >= 0)) {
                // don't need to update if it was correct
            } else {
                // if not the same then update, not the end of the world if it fails
                const result =await getFirestore()
                    .collection("all")
                    .doc("imageFiles")
                    .update({imageFilenames: updatedFileNames});
                deletedFromList = true;
            }

            res.send(`deleted file from storage: ${deletedFromStorage}. deleted filename from list: ${deletedFromList}`);
            
        }else{
            res.send(`Error: ${err.message}`);
            return
        }
      });
});

// Create and deploy your first functions
// https://firebase.google.com/docs/functions/get-started

// exports.helloWorld = onRequest((request, response) => {
//   logger.info("Hello logs!", {structuredData: true});
//   response.send("Hello from Firebase!");
// });