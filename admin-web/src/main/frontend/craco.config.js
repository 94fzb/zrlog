module.exports = {
    webpack: {
        configure: (webpackConfig) => {
            webpackConfig.optimization.splitChunks = {
                chunks: 'all',
                cacheGroups: {
                    vendor: {
                        test: /[\\/]node_modules[\\/](react|react-dom|antd|react-router|react-router-dom|styled-components|@ant-design\/cssinjs)[\\/]/,
                        name: 'vendor',
                        chunks: 'all',
                    },
                },
            };
            webpackConfig.output = {
                ...webpackConfig.output,
                filename: 'static/js/[name].[contenthash:8].js',
                chunkFilename: 'static/js/[name].[contenthash:8].chunk.js',
            };
            return webpackConfig;
        },
    },
};