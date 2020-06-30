
exports.up = function (knex) {
  return knex.schema
    .createTable('order', table => {
      table.increments('id')
      table.string('status', 20).notNullable()
    })
}

exports.down = function (knex) {
  return knex.schema
    .dropTable('order')
}
