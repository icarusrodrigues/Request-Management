ALTER TABLE IF EXISTS public.users
    ADD COLUMN username text NOT NULL;

ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT unique_username UNIQUE (username);

ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT unique_cpf UNIQUE (cpf);

ALTER TABLE IF EXISTS public.users
    ADD CONSTRAINT unique_email UNIQUE (email);