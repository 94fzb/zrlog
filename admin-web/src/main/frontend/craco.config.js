const CracoLessPlugin = require("craco-less");

module.exports = {
    watchOptions: {
        ignored: ['**/public/vendors/**', '**/node_modules'],
    },
    plugins: [
        {
            plugin: CracoLessPlugin,
            options: {
                lessLoaderOptions: {
                    lessOptions: {
                        javascriptEnabled: true,
                    },
                },
            },
        },
    ],
};
