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

function openSideBar(){
  sidebar.toggle();
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
