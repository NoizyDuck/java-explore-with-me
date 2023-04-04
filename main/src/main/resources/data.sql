delete FROM categories;
delete FROM compilations;
delete FROM users;
delete FROM events;
delete FROM event_compilations;
delete FROM requests;
delete FROM locations;

ALTER TABLE categories ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE compilations ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE users ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE events ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE event_compilations ALTER COLUMN compilation_id RESTART WITH 1;
ALTER TABLE requests ALTER COLUMN ID RESTART WITH 1;
ALTER TABLE locations ALTER COLUMN ID RESTART WITH 1;