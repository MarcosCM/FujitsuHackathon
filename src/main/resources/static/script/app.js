var app = angular.module("myApp", []);
app.controller("myCtrl", function($scope, $http) {

  $scope.markerData = {};

  //Get camera locations from address (cuatro+caminos+santander)
  $http.get('https://maps.googleapis.com/maps/api/geocode/json?address=cuatro+caminos+santander&key=AIzaSyBb6qPOlPv8BXjQuBgUhH1bxK815o35nig')
  .success(function(data, status, headers, config) {
    if (data.status == "OK"){
      L.marker([data.results[0].geometry.location.lat,data.results[0].geometry.location.lng],{icon: blueMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/350x150'}).addTo($scope.map).on('click', onClick);
      //We add some virutal markers to improve general idea for presentation
      L.marker([43.492259, -3.818231],{icon: redMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/352x288'}).addTo($scope.map).on('click', onClick);
      L.marker([43.488729, -3.803640],{icon: blueMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/352x288'}).addTo($scope.map).on('click', onClick);
      L.marker([43.484592, -3.788019],{icon: blueMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/352x288'}).addTo($scope.map).on('click', onClick);
      L.marker([43.482774, -3.807846],{icon: blueMarker, title: 'cuatro_caminos', type: 'OK', img: 'http://placehold.it/352x288'}).addTo($scope.map).on('click', onClick);

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

    $scope.markerData.img = this.options.img;
    $scope.markerData.title = this.options.title;
    $scope.markerData.type = this.options.type;

    var d = new Date();
    console.log(d.getFullYear()+'/'+(d.getMonth()+1)+'/'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds());

    $http.get('/get_data?camera='+this.options.title+'&date='+d.getFullYear()+'-'+(d.getMonth()+1)+'-'+d.getDate()+' '+d.getHours()+':'+d.getMinutes()+':'+d.getSeconds())
    .success(function(data, status, headers, config) {
      $scope.markerData.img = data.img;
    })
    .error(function(error, status, headers, config) {
      console.log("Error occured");
    });


    console.log($scope.markerData);
    $scope.$apply();
    openSideBar();
  }

  $scope.map = map;
  $scope.sidebar = sidebar;

});