$(document).ready(function() {
  var config = {
    apiKey: "AIzaSyB9hrPS41Gqy9rKN_u5Yj2P7Qrh26kwRXo",
    authDomain: "cbc-hackathon-2019.firebaseapp.com",
    databaseURL: "https://cbc-hackathon-2019.firebaseio.com",
    projectId: "cbc-hackathon-2019",
    storageBucket: "cbc-hackathon-2019.appspot.com",
    messagingSenderId: "611365118872"
  };
  firebase.initializeApp(config);

  document.addEventListener("new_id", function(e) {
    console.log("Saving to firebase", e.detail);
    var child = firebase.database().ref().child('queue').push();
    child.set(e.detail);
  });

  let url = window.location.href;
  let lang = "";
  let idRegex = null;
  if (~url.indexOf("radio-canada.ca")) {
    // eg: https://ici.radio-canada.ca/nouvelle/1150640/venezuela-caracas-tensions-manifestations-maduro-guaido
    idRegex = /^.*\/([0-9]+)\/.*$/g;
    lang = "fr";
  } else if (~url.indexOf("cbc.ca")) {
    // eg: https://www.cbc.ca/news/canada/montreal/mystery-solved-we-finally-know-how-the-snow-bear-got-its-belly-button-1.5002854
    idRegex = /^.*\-\d+\.([0-9]+)$/g;
    lang = "en";
  }
  if (idRegex) {
    let id_matches = idRegex.exec(url);
    if (id_matches && id_matches.length > 1) {
      let id = id_matches[1];

      document.body.innerHTML +=
        '<div style="position:fixed;width:50px;height:50px;opacity:0.3;z-index:100000;top:75px;right:20px;border-radius:50%;background:#000;cursor:pointer;" onClick="document.dispatchEvent(new CustomEvent(\'new_id\', {detail: {id:\'' +
        id +
        '\', lang:\'' +
        lang +
        '\'}}));"> <img src="https://developer.android.com/images/brand/Android_Robot.png" style="width: 21px;height: 25px;margin: 50%;transform: translate(-50%, -50%);"></img></div>';

      console.log("Import tensorflow as tf: matched id", id);
    }
  }
});
