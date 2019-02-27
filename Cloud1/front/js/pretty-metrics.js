window.onload = getMetrics;

function getMetrics() {
    let adv = 'https://api.adviceslip.com/advice';
    let translation = 'https://api.funtranslations.com/translate';
    let polly = 'AWSPolly';
    let full = '/advice';
    let xhr = new XMLHttpRequest();
    xhr.open("GET", "/metrics", true);
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            // console.log(xhr.responseText);
            var response = JSON.parse(xhr.responseText);
            console.log(response[adv]);
            console.log(response[translation]);
            console.log(response[polly]);
            console.log(response[full]);
            document.getElementById("advice").innerHTML += '<p class="content"><b>Average Latency: </b>' + response[adv][0] + 'ms<br><b>Latest known code: </b>' + response[adv][1]+ '</p>'
            document.getElementById("translation").innerHTML += '<p class="content"><b>Average Latency: </b>' + response[translation][0] + 'ms<br><b>Latest known code: </b>' + response[translation][1]+ '</p>'
            document.getElementById("polly").innerHTML += '<p class="content"><b>Average Latency: </b>' + response[polly][0] + 'ms<br><b>Latest known code: </b>' + response[polly][1]+ '</p>'
            document.getElementById("full").innerHTML += '<p class="content"><b>Average Latency: </b>' + response[full][0] + 'ms<br><b>Latest known code: </b>' + response[full][1]+ '</p>'

        }
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 429) {
        }
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 500) {
        }
    };

    xhr.send();
}
