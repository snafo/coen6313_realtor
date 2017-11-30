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
                        var param = {
                            'lat' : $scope.lat,
                            'lng' : $scope.lng,
                            'type': vm.type,
                            'area': vm.area,
                            'year': vm.year,
                            'bedroom': vm.bedroom,
                            'bathroom': vm.bathroom
                        };
                        QueryServices.estimatePrice(param)
                            .then(function (result){
                                if (result.code === 1 ) {
                                    $scope.size = result.payLoad.size;
                                    $scope.price = result.payLoad.price;
                                } else {
                                    alert(result.message);
                                }
                            });

                    }
                });
            }
        }

        //

// Heatmap data: 500 Points

    });