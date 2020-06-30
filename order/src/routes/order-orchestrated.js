const express = require('express')
const router = express.Router()
const knex = require('../knex')
const kafka = require('../kafka-producer')
const DEFAULT_STATUS = 'PENDING'

router.get('/', (req, res) => {
  knex.select().from('order')
    .then((rows) => {
      res.send(rows)
    })
})

router.post('/', (req, res) => {
  knex.insert({ status: DEFAULT_STATUS })
    .into('order')
    .then((id) => {
      kafka.send('order_request', JSON.stringify({ order: id[0], status: DEFAULT_STATUS }))
      res.status(201).send(id)
    })
})

module.exports = router
