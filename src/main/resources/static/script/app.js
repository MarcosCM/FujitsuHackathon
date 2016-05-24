var app = angular.module("myApp", []);
app.controller("myCtrl", function($scope, $http) {

  $scope.markerData = {};

  //GET clases
  $http.get('https://maps.googleapis.com/maps/api/geocode/json?address=cuatro+caminos+santander&key=AIzaSyBb6qPOlPv8BXjQuBgUhH1bxK815o35nig')
  .success(function(data, status, headers, config) {
    if (data.status == "OK"){
      //addMarker(data.results[0].geometry.location.lat,data.results[0].geometry.location.lng,'TITLE TEST','OK','http://placehold.it/350x150');
      L.marker([data.results[0].geometry.location.lat,data.results[0].geometry.location.lng],{icon: blueMarker, title: 'TITULO', type: 'OK', img: 'http://placehold.it/350x150'}).addTo($scope.map).on('click', onClick);
    }
  })
  .error(function(error, status, headers, config) {
    console.log("Error occured");
  });

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

  function onClick() {
    $scope.markerData.title = this.options.title;
    $scope.markerData.type = this.options.type;
    $scope.markerData.img = this.options.img;
    console.log($scope.markerData);
    $scope.$apply();
    openSideBar();
  }

  $scope.map = map;
  $scope.sidebar = sidebar;

});
