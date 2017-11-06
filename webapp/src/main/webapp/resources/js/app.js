// Default colors
var brandPrimary =  '#20a8d8';
var brandSuccess =  '#4dbd74';
var brandInfo =     '#63c2de';
var brandWarning =  '#f8cb00';
var brandDanger =   '#f86c6b';

var grayDark =      '#2a2c36';
var gray =          '#55595c';
var grayLight =     '#818a91';
var grayLighter =   '#d1d4d7';
var grayLightest =  '#f8f9fa';

angular
    .module('app', [
        'ui.router',
        'ui.bootstrap',
        'angular-momentjs',
        'daterangepicker',
        'restangular',
        'ui.grid',
        'ui.grid.pagination',
        'ui.grid.resizeColumns',
        'ui.grid.moveColumns',
        'ui.grid.expandable',
        'ui.grid.selection',
        'ui.grid.edit',
        'ui.grid.rowEdit',
        'ui.grid.cellNav',
        'ui.grid.autoResize',
        'app.services',
        'app.controllers',
        'app.routing'
    ])
    .run(['$rootScope', '$state', '$stateParams', 'Restangular', function($rootScope, $state, $stateParams, Restangular) {
        $rootScope.$on('$stateChangeSuccess',function(){
            document.body.scrollTop = document.documentElement.scrollTop = 0;
        });
        Restangular.setBaseUrl('rest/api/');
        $rootScope.$state = $state;
        $rootScope.$stateParams = $stateParams;

    }]);
