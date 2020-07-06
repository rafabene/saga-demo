
exports.up = function (knex) {
  return knex.schema
    .createTable('order', table => {
      table.increments('id')
      table.string('status', 20).notNullable()
      table.string('canceledCause', 100).nullable()
    })
}

exports.down = function (knex) {
  return knex.schema
    .dropTable('order')
}
