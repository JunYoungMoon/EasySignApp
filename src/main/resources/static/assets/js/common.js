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
});