const express = require('express')
const kafka = require('./kafka-producer')
const knex = require('./knex')
const orderOrchestration = require('./routes/order-orchestrated')
const app = express()
const port = 3000

app.get('/', (req, res) => res.send('Please, access /orchestration/order'))

app.use('/orchestration/order', orderOrchestration)

app.listen(port, () => {
  console.log('Creating table...')
  knex.migrate.latest()
  console.log('Table created!')
  console.log(`Order microservice listening at http://0.0.0.0:${port}`)
})
