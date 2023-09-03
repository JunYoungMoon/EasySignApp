function showloading() {
    console.log('Loading...');
}

function hideloading() {
    console.log('Loading finished.');
}

function ajaxRequest(url, method, data, callback) {
    let xhr = new XMLHttpRequest();

    xhr.onloadstart = function () {
        showloading();
    };

    xhr.open(method, url, true);

    xhr.setRequestHeader('Content-Type', 'application/json');
    if(data){
        if(data.accessToken){
            xhr.setRequestHeader('Authorization', 'Bearer ' + data.accessToken);
        }
        if(data.csrfToken){
            xhr.setRequestHeader('X-XSRF-TOKEN', data.csrfToken);
        }
    }

    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4) {
            hideloading();
            if (xhr.status === 200) {
                callback(xhr.responseText);
            } else {
                console.error('Request failed with status:', xhr.status);
            }
        }
    };

    xhr.send(data);
}

// 페이지 로딩 시 실행
window.addEventListener('load', function () {
    ajaxRequest('/getcsrf', 'GET', null, function (response) {
        let csrfToken = JSON.parse(response);
        localStorage.setItem('csrfToken', csrfToken.token);
        console.log('CSRF token stored in localStorage:', csrfToken.token);
    });

    // Step 1: Retrieve AccessToken from cookie
    const accessToken = getCookie('accessToken');

    // Step 2: Retrieve CSRF Token from localStorage
    const csrfToken = localStorage.getItem('csrfToken');

    // Step 3: Send a request to the server with AccessToken and CSRF Token
    ajaxRequest('/test', 'POST', {
        accessToken: accessToken,
        csrfToken: csrfToken
    }, function (response) {
        // Handle the server response here
    });
});

// Function to retrieve a cookie by name
function getCookie(name) {
    const cookies = document.cookie.split(';');
    for (let i = 0; i < cookies.length; i++) {
        const cookie = cookies[i].trim();
        if (cookie.startsWith(name + '=')) {
            return cookie.substring(name.length + 1);
        }
    }
    return null;
}