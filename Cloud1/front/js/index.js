window.onload = init;
var context;    // Audio context
var buf;        // Audio buffer

function init() {
    if (!window.AudioContext) {
        if (!window.webkitAudioContext) {
            alert("Your browser does not support any AudioContext and cannot play back this audio.");
            return;
        }
        window.AudioContext = window.webkitAudioContext;
    }

    context = new AudioContext();
}

function playByteArray(byteArray) {

    var arrayBuffer = new ArrayBuffer(byteArray.length);
    var bufferView = new Uint8Array(arrayBuffer);
    for (i = 0; i < byteArray.length; i++) {
        bufferView[i] = byteArray[i];
    }

    context.decodeAudioData(arrayBuffer, function (buffer) {
        buf = buffer;
        play();
    });
}

function play() {
    // Create a source node from the buffer
    var source = context.createBufferSource();
    source.buffer = buf;
    // Connect to the final output node (the speakers)
    source.connect(context.destination);
    // Play immediately
    source.start(0).catch(function() {
       console.log("aici??");
        // do something
    });
}

function create_music(id){
    var sound      = document.createElement('audio');
    sound.id       = 'audio-player';
    sound.controls = 'controls';
    sound.src      = 'res/' + id + '.mp3';
    sound.type     = 'audio/mpeg';
    document.getElementById('text-div').appendChild(sound);
}


function getFact() {
    document.getElementById("orcish").style.display = 'none';
    document.getElementById("drogo").style.display = 'none';
    document.getElementById("minion").style.display = 'none';
    document.getElementById("pirate").style.display = 'none';
    document.getElementById("shakespeare").style.display = 'none';
    document.getElementById("yoda").style.display = 'none';
    document.getElementById("text-div").innerHTML = "";
    document.getElementById("loading").style.display = 'block';
    document.getElementById("tooManyReq").style.display = 'none';
    document.getElementById("internal").style.display = 'none';

    let xhr = new XMLHttpRequest();
    xhr.open("GET", "/advice", true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            document.getElementById("loading").style.display = 'none';
            console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            console.log(xhr.responseText);

            var para = document.createElement("P");                       // Create a <p> node
            var t = document.createTextNode(response.original)                      // Create a text node
            para.appendChild(t);
            para.innerHTML += '<br> <br>';

            para.appendChild(document.createTextNode(response.translation));
            document.getElementById("text-div").appendChild(para);


            if(response.language === 'yoda'){
                console.log("yoda");
                document.getElementById("yoda").style.display = 'block';
            }


            if(response.language === 'orcish'){
                console.log("orcish");
                document.getElementById("orcish").style.display = 'block';
            }

            if(response.language === 'dothraki'){
                console.log("drogo");
                document.getElementById("drogo").style.display = 'block';
            }

            if(response.language === 'pirate'){
                console.log("pirate");
                document.getElementById("pirate").style.display = 'block';
            }

            if(response.language === 'shakespeare'){
                console.log("shakespeare");
                document.getElementById("shakespeare").style.display = 'block';
            }

            if(response.language === 'minion'){
                console.log("minion");
                document.getElementById("minion").style.display = 'block';
            }

            // decodeURIComponent(escape(window.atob( str )))
            // var audiofile = window.atob(txt.audio.replace(/\s/g, ''));
            // playByteArray(audiofile)
            create_music(response.fileName)
            
        }
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 429){
            document.getElementById("loading").style.display = 'none';
            document.getElementById("tooManyReq").style.display = 'block';
        }
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 500){
            document.getElementById("loading").style.display = 'none';
            document.getElementById("internal").style.display = 'block';
        }
    };

    //TODO: add a 'loading' gif while waiting for response
    xhr.send();

    // xmlHttp.open("GET", 'http://localhost:6969/facts', true); // false for synchronous request
    // xmlHttp.send(null);
    // console.log("aLo??")
    // console.log(xmlHttp.responseText);
    // document.getElementById("text-div").textContent = xmlHttp.responseText
    // var audio = window.atob(xmlHttp.response)
    // let xhr = new XMLHttpRequest();
    // console.log("first");
    // xhr.onreadystatechange = function() {
    //     if(this.readyState === 4 && this.status === 201) {
    //         console.log("great success");
    //     }
    // };
    // console.log("second");
    // xhr.open("GET", "/facts", true);
    // xhr.send(crisisJson);


}