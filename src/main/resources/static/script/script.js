var map = L.map('map');
map.setView([43.462932, -3.811766], 13);
L.tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
  maxZoom: 18,
  attribution: 'Map data &copy; OpenStreetMap contributors'
}).addTo(map);
var sidebar = L.control.sidebar('sidebar', {
  closeButton: true,
  position: 'left'
});
map.addControl(sidebar);

setTimeout(function () {
  sidebar.show();
}, 500);

var redMarker = L.AwesomeMarkers.icon({
  icon: 'ion-alert-circled',
  prefix: 'ion',
  markerColor: 'red'
});

  var blueMarker = L.AwesomeMarkers.icon({
  icon: 'ion-information-circled',
  prefix: 'ion',
  markerColor: 'blue'
});

var obJS = {title:'',
  type:'',
  img: ''
};

function addMarker (lat,lng,name,type,imgUrl) {
  obJS.title = name;
  obJS.img = imgUrl;
  obJS.type = type;

  if(type === 'OK'){
    var marker = L.marker([lat, lng],{icon: blueMarker}).addTo(map).on('click', function () {
      sidebar.toggle();
    });
  }
  else{
    var marker2 = L.marker([lat, lng],{icon: redMarker}).addTo(map).on('click', function () {
      sidebar.toggle();

    });
  }
}

map.on('click', function () {
  sidebar.hide();
});
sidebar.on('show', function () {
  console.log('Sidebar will be visible.');
});
sidebar.on('shown', function () {
  console.log('Sidebar is visible.');
});
sidebar.on('hide', function () {
  console.log('Sidebar will be hidden.');
});
sidebar.on('hidden', function () {
  console.log('Sidebar is hidden.');
});
L.DomEvent.on(sidebar.getCloseButton(), 'click', function () {
  console.log('Close button clicked.');
});
