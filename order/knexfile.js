const path = require('path')

module.exports = {

  development: {
    client: 'mysql2',
    version: '8',
    debug: false,
    connection: {
      host: process.env.DB_HOST === undefined ? '127.0.0.1' : process.env.DB_HOST,
      user: process.env.DB_USERNAME === undefined ? 'myuser' : process.env.DB_USERNAME,
      password: process.env.DB_PASSWORD === undefined ? 'mypassword' : process.env.DB_PASSWORD,
      database: 'sagademo'
    },
    pool: { min: 0, max: 2 },
    migrations: {
      directory: path.resolve('./src/knex/migrations')
    },
    seeds: {
      directory: path.resolve('./src/knex/seeds')
    }
  }

}
