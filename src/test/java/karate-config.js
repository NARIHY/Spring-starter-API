function fn() {
  var env = karate.env || 'dev'; // Default to 'dev' if no environment is set
  karate.log('Environment is:', env);

  var config = {
    baseUrl: 'http://localhost:8080' // Replace with your default URL
  };

  if (env === 'dev') {
    config.baseUrl = 'http://localhost:8080';
  } else if (env === 'prod') {
    config.baseUrl ='http://localhost:8080';
  }

  karate.log('Base URL is:', config.baseUrl);
  return config;
}
