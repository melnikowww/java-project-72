create table Url (
  id                            PRIMARY KEY GENERATED ALWAYS AS IDENTITY not null,
  name                          varchar(255),
  created_at                    timestamp not null
);