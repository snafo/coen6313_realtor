angular.module('app.controllers').
    controller('cribsController', function ($scope, $rootScope,cribsFactory, QueryServices) {

        $scope.cribs;

        var vm = this;

        vm.user = null;

        initController();

        function initController() {
            loadCurrentUser();
        }

        function loadCurrentUser() {
            QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
                .then(function (result) {
                    if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                        vm.user = result.payLoad[0].name;
                    } else {
                        vm.user = $rootScope.globals.currentUser.name;
                    }
                });
        }


        QueryServices.getProperty(
            {
                'price':'{lt:500000}',
                'year':'{gt:1990}',
                'type': 'House',
                'area': '{gt:1000}',
                'limit': 1
            }
        ).then(function (response){
            console.log(response.payLoad);
        });

        $scope.priceInfo = {
            min:0,
            max:1000000
        };

        $scope.newListing = {};

        $scope.addCrib = function (newListing) {
            newListing.image='resources/image/default-crib';
            $scope.cribs.push(newListing);
            $scope.newListing={};
        };

        $scope.editCrib = function (crib) {
            $scope.editListing = true;
            $scope.existingListing = crib;
        };

        $scope.saveCribEdit = function () {
            $scope.existingListing = {};
            $scope.editListing = false;
        };

        $scope.deleteCrib = function (listing) {
            var index = $scope.cribs.indexOf(listing);
            $scope.cribs.splice(index,1);
            $scope.existingListing = {};
            $scope.existingListing = false;
        };

        cribsFactory.getCribs().then(function (data) {
            $scope.cribs=data.data;
        }, function(error) {
            console.log(error);
        });
        //
        // $scope.sayHello = function () {
        //     console.log("hello");
        // }
    });