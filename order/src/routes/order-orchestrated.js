const express = require('express')
const router = express.Router()
const knex = require('../knex')
const kafka = require('../kafka-processor')
const DEFAULT_STATUS = 'PENDING'

router.get('/', (req, res) => {
  knex.select().from('order')
    .then((rows) => {
      res.send(rows)
    })
})

router.put('/:id', (req, res) => {
  if (req.body.status !== 'CANCEL' && req.body.status !== 'CONFIRM') {
    res.status(400).send('Only \'CANCEL\' and \'CONFIRM\' can be informed as status')
  } else {
    // Valid parameters
    knex('order')
      .where('id', req.params.id)
      .then((order) => {
        // No order
        if (order.length === 0) {
          res.status(404).send(`No order with the id ${req.params.id}`)
        } else if (order[0].status === 'PENDING') {
          const newStatus = req.body.status + 'ED'
          knex('order').where('id', req.params.id)
            .update('status', newStatus)
            .then((id) => {
              res.send(JSON.stringify({ id: id, status: newStatus }))
            })
        } else {
          res.status(400).send(`Order ${order[0].id} has status ${order[0].status}`)
        }
      })
  }
})

router.post('/', (req, res) => {
  knex.insert({ status: DEFAULT_STATUS })
    .into('order')
    .then((id) => {
      kafka.send('order_request', JSON.stringify({ id: id[0], status: DEFAULT_STATUS }))
      res.status(201).type('.txt').send(id[0].toString())
    })
})

module.exports = router
