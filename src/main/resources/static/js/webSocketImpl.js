var stompClient = null;

function setConnected(connected) {
  document.getElementById('conversationDiv').style.visibility = connected ? 'visible' : 'hidden';
}

function connect() {
  var socket = new SockJS('/stadistics');
  stompClient = Stomp.over(socket);
  stompClient.connect({}, function(frame) {
    setConnected(true);
    console.log('Connected: ' + frame);
    //lo que haces aqui es subscribirte, los mensajes destinados a /sockets/urlID
    //se duplican y llegan automaticamente a todos los subscritos
    var urlActual= document.URL.split("/");
    var idActual=urlActual[3].substring(0, urlActual[3].length-1);
    var subscripcion='/sockets/'+idActual;
    stompClient.subscribe(subscripcion, function(estadisticas){
      var clicks=JSON.parse(estadisticas.body).clicks;
      var url=JSON.parse(estadisticas.body).url;
      var fecha=JSON.parse(estadisticas.body).fechaCreacion;
      var response = document.getElementById('response');
      response.innerHTML="Número de clicks: "+clicks+"<br/>"+
      "Url: "+url+"<br/>"+"Fecha: "+fecha;
    });
  });
}

function sendInformation() {
  //si se necesita enviar informacion por socket
  stompClient.send("/app/stadistics", {}, JSON.stringify({ 'name': 'esto es lo que se en manda en Json' }));
}
