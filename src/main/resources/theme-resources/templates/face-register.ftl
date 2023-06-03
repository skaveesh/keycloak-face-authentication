<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=true; section>
  <#if section = "title">
    Authenticate with your Face
  <#elseif section = "header">
    Please authenticate with your Face
  <#elseif section = "form">

    <div style="position: relative" class="margin">
      <video onloadedmetadata="onPlay(this)" id="inputVideo" style="width:100%" autoplay muted playsinline>
        Loading Face Auth...
      </video>
      <#--            <canvas id="overlay"></canvas>-->
    </div>

  <#--        <script src="http://localhost:3000/face-api.js"></script>-->

    <script src="${url.resourcesPath}/js/face-api.min.js" defer></script>

    <script defer>
      const SSD_MOBILENETV1 = 'ssd_mobilenetv1'

      let selectedFaceDetector = SSD_MOBILENETV1

      // ssd_mobilenetv1 options
      let minConfidence = 0.5

      // tiny_face_detector options
      let inputSize = 128
      let scoreThreshold = 0.5

      function getFaceDetectorOptions() {
        return new faceapi.SsdMobilenetv1Options({minConfidence});
      }

      function getCurrentFaceDetectionNet() {
        return faceapi.nets.ssdMobilenetv1
      }

      function isFaceDetectionModelLoaded() {
        return !!getCurrentFaceDetectionNet().params
      }

      const minFaceScoreThreshold = 0.7;

      const $inputVideo = document.getElementById("inputVideo");
      const $overlay = document.getElementById("overlay");

      let analyzingFace = false;

      async function onPlay() {
        document.getElementById("captureButton").disabled = true;
        if ($inputVideo.paused || $inputVideo.ended || !isFaceDetectionModelLoaded()) {
          return setTimeout(() => onPlay(), 1000)
        }

        if (analyzingFace) {
          setTimeout(() => onPlay(), 1000)
          return;
        }

        const options = getFaceDetectorOptions();
        const singleFaceResult = await faceapi.detectSingleFace($inputVideo, options)

        if (!singleFaceResult) {
          setTimeout(() => onPlay(), 1000);
          return;
        }

        if (singleFaceResult.score < minFaceScoreThreshold) {
          setTimeout(() => onPlay(), 1000);
          return;
        }

        if (window.faceResult) {
          setTimeout(() => onPlay(), 1000);
          return;
        }

        console.log("found face");
        analyzingFace = true;

        // for debugging
        window.faceResult = singleFaceResult;

        console.log("use face...");

        let faceCanvases = await faceapi.extractFaces($inputVideo, [singleFaceResult.box]);
        if (faceCanvases.length === 0) {
          setTimeout(() => onPlay(), 1000);
          return;
        }

        let faceCanvas = faceCanvases[0];
        let faceBlob = await toBlob(faceCanvas);

        let dataUrl = await readAsDataURL(faceBlob);

        document.getElementById("imageCanvas").value = dataUrl;
        document.getElementById("captureButton").disabled = false;
        document.getElementById("capturedImage").src = dataUrl;

        window.faceResult = null;
        analyzingFace = false;

        console.log("try again");
        setTimeout(() => onPlay(), 1000);
      }

      async function toBlob(canvas) {
        return new Promise((resolve, reject) => {
          canvas.toBlob(blob => resolve(blob));
        });
      }

      async function readAsDataURL(blob) {
        return new Promise((resolve, reject) => {
          let fr = new FileReader();
          fr.onload = () => {
            return resolve(fr.result);
          };
          fr.readAsDataURL(blob);
        });
      }

      async function startFaceDetection() {
        // load face detection model
        // await changeFaceDetector(TINY_FACE_DETECTOR)
        // changeInputSize(128)

        if (!isFaceDetectionModelLoaded()) {
          // await getCurrentFaceDetectionNet().load('http://localhost:3000/')
          await getCurrentFaceDetectionNet().load('${url.resourcesPath}/js/')

        }

        // try to access users webcam and stream the images
        // to the video element
        const stream = await navigator.mediaDevices.getUserMedia({video: {}})
        $inputVideo.srcObject = stream
      }

      function runScripts(element) {
        var list, scripts, index;

        // Get the scripts
        list = element.getElementsByTagName("script");
        scripts = [];
        for (index = 0; index < list.length; ++index) {
          scripts[index] = list[index];
        }
        list = undefined;

        // Run them in sequence
        continueLoading();

        function continueLoading() {
          var script, newscript;

          // While we have a script to load...
          while (scripts.length) {
            // Get it and remove it from the DOM
            script = scripts[0];
            script.parentNode.removeChild(script);
            scripts.splice(0, 1);

            console.log("Running script: " + script.src);

            // Create a replacement for it
            newscript = document.createElement('script');

            // External?
            if (script.src) {
              // Yes, we'll have to wait until it's loaded before continuing
              newscript.onerror = continueLoadingOnError;
              newscript.onload = continueLoadingOnLoad;
              newscript.onreadystatechange = continueLoadingOnReady;
              newscript.src = script.src;
            } else {
              // No, we can do it right away
              newscript.text = script.text;
            }

            // Start the script
            document.documentElement.appendChild(newscript);

            // If it's external, wait
            if (script.src) {
              return;
            }
          }

          // All scripts loaded
          newscript = undefined;

          // Callback on most browsers when a script is loaded

          function continueLoadingOnLoad() {
            // Defend against duplicate calls
            if (this === newscript) {
              continueLoading();
            }
          }

          // Callback on most browsers when a script fails to load

          function continueLoadingOnError() {
            // Defend against duplicate calls
            if (this === newscript) {
              continueLoading();
            }
          }

          // Callback on IE when a script's loading status changes

          function continueLoadingOnReady() {

            // Defend against duplicate calls and check whether the
            // script is complete (complete = loaded or error)
            if (this === newscript && this.readyState === "complete") {
              continueLoading();
            }
          }
        }
      }

      let waitForFaceApi = function () {
        if (window.faceapi) {
          console.log("faceapi loaded...")
          startFaceDetection();
        } else {
          console.log("waiting for faceapi to load...")
          setTimeout(waitForFaceApi, 500);
        }
      }

      setTimeout(waitForFaceApi, 1000);

    </script>

    <form id="kc-u2f-register-form" class="${properties.kcFormClass!}" action="${url.registrationAction}" method="post">
      <div class="${properties.kcFormGroupClass!}">
        <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
          <img src="${url.resourcesPath}/images/profile.jpg" id="capturedImage" width="100" height="100" alt="Captured Image"/>
          <input id="imageCanvas" name="imageCanvas" type="text" hidden />
          <button id="captureButton" type="submit" value="Capture">Capture</button>
        </div>
      </div>
    </form>
  </#if>

</@layout.registrationLayout>