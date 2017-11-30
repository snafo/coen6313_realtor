angular
    .module('app.controllers')
    .controller('estimationController', function ($scope, QueryServices) {

        var vm = this;

        vm.estimate = estimate;

        function estimate() {
            // Initialize the Geocoder
            geocoder = new google.maps.Geocoder();
            if (geocoder) {
                geocoder.geocode({
                    'address': vm.address
                }, function (results, status) {
                    if (status == google.maps.GeocoderStatus.OK) {
                        $scope.lat = results[0].geometry.location.lat();
                        $scope.lng = results[0].geometry.location.lng();

                        QueryServices.estimatePrices()
                            .then(function (result){
                                if (result.code === 1 ) {
                                    datas = result.payLoad;
                                    datas.forEach(function(data){
                                        var latLng = new google.maps.LatLng(data.location.lat, data.location.lng);
                                        if(data.area !== null ) {
                                            $scope.heatmapData.push({location: latLng, weight: data.price / (data.area * 1000)});
                                        }
                                    });
                                } else {
                                    alert("Failed to load data");
                                }
                            });

                        console.log($scope.heatmapData);

                    }
                });
            }
        }

        //

// Heatmap data: 500 Points

    });