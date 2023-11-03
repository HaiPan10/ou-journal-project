window.onload = function() {
    moment.locale('vi');
    var dateElements = document.getElementsByClassName('dateElement');
    for (var i = 0; i < dateElements.length; i++) {
        var date = dateElements[i].innerText;
        var formattedDate = moment(date).fromNow();
        dateElements[i].innerText = formattedDate;
    }
}