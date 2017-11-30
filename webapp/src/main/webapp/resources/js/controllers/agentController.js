
angular.module('app.controllers').
controller('AgentController', function ($scope, $rootScope, QueryServices, NgMap) {
    $rootScope.currentTab = 'agent';

    $scope.cribs;

    $scope.saveDisabled =false;

    $scope.user = {};

    NgMap.getMap().then(function (map) {
        $scope.map = map;
    });

    $scope.showProp = function(event, prop) {
        $scope.selectedProp = prop;
        $scope.map.showInfoWindow('myInfoWindow',prop.propertyId.toString());
    };

    initController();
    function initController() {
        QueryServices.getUserByNameCustom($rootScope.globals.currentUser.name)
            .then(function (result) {
                if (result.payLoad[0].name === $rootScope.globals.currentUser.name) {
                    $scope.user = result.payLoad[0];
                    QueryServices.getRecommend(result.payLoad[0].id)
                        .then(function (result){
                            if (result.code === 1 ) {
                                $scope.cribs = result.payLoad;
                            } else {
                                alert(result.message);
                            }
                        });
                } else {
                    // vm.user = $rootScope.globals.currentUser;
                    // console.log("cannot map");
                }
            });
    }

    $scope.createFavorite = function (crib) {
        var param = {uid: $scope.user.id ,propertyid: crib.propertyId};
        console.log(param);
        QueryServices.createFavorite(param).then(function (result){
            if(result.code === 1){
                var index = $scope.cribs.indexOf(crib);
                $scope.cribs[index].favorite = true;
            } else{
                alert("Failed to save to favorite list");
            }
        });
    };
});

