const kafkaurl = process.env.KAFKA_URL ? process.env.KAFKA_URL : 'localhost:19092'
console.log(`Configuring KAFKA URL as ${kafkaurl}`)
const kafka = require('kafka-node')
const Producer = kafka.Producer
const client = new kafka.KafkaClient({ kafkaHost: kafkaurl })
const producer = new Producer(client)

producer.on('error', function (err) {
  console.log(err)
})
producer.on('ready', function () {
  console.log('Kafka Ready!')

  function createTopic (topic) {
    const topicsToCreate = [{
      topic: topic,
      partitions: 1,
      replicationFactor: 1
    }]
    client.createTopics(topicsToCreate, (_error, result) => {
      console.log(result || '')
      // result is an array of any errors if a given topic could not be created
    })
  }
  createTopic('order_request')
  createTopic('order_response')
})

module.exports = {

  send (topic, msg) {
    producer.send([{ topic: topic, messages: msg, partition: 0 }], function (err, data) {
      if (err) {
        console.log(err)
      }
    })
  }

}
