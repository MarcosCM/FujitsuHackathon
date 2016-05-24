var app = angular.module("myApp", []);
app.controller("myCtrl", function($scope, $http) {

  $scope.markerData = {};

  //GET clases
  $http.get('https://maps.googleapis.com/maps/api/geocode/json?address=cuatro+caminos+santander&key=AIzaSyBb6qPOlPv8BXjQuBgUhH1bxK815o35nig')
  .success(function(data, status, headers, config) {
    if (data.status == "OK"){
      //addMarker(data.results[0].geometry.location.lat,data.results[0].geometry.location.lng,'TITLE TEST','OK','http://placehold.it/350x150');
      L.marker([data.results[0].geometry.location.lat,data.results[0].geometry.location.lng],{icon: blueMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/350x150'}).addTo($scope.map).on('click', onClick);
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

    var d = new Date();
    console.log(d.getFullYear()+'/'+(d.getMonth()+1)+'/'+d.getDate()+'_'+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds());

    $http.get('/get_data?camera='+this.options.title+'&date='+d.getFullYear()+'/'+(d.getMonth()+1)+'/'+d.getDate()+'_'+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds())
    .success(function(data, status, headers, config) {
      $scope.markerData.img = data.img;
    })
    .error(function(error, status, headers, config) {
      console.log("Error occured");
    });

    $scope.markerData.title = this.options.title;
    $scope.markerData.type = this.options.type;

    console.log($scope.markerData);
    $scope.$apply();
    openSideBar();
  }

  $scope.map = map;
  $scope.sidebar = sidebar;

});
