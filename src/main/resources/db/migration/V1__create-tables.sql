CREATE TABLE public.users
(
    id bigint NOT NULL,
    code text NOT NULL,
    cpf text NOT NULL,
    email text NOT NULL,
    registration_number text NOT NULL,
    name text NOT NULL,
    password text NOT NULL,
    birth_date date NOT NULL,
    gender text,
    user_type text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.requests
(
    id bigint NOT NULL,
    code text NOT NULL,
    area text NOT NULL,
    request_type text NOT NULL,
    workload bigint NOT NULL,
    total_cost real NOT NULL,
    user_type text NOT NULL,
    owner_id bigint NOT NULL,
    PRIMARY KEY (id)
);

ALTER TABLE IF EXISTS public.users
    OWNER to postgres;

ALTER TABLE IF EXISTS public.requests
    OWNER to postgres;

CREATE SEQUENCE public.user_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

CREATE SEQUENCE public.request_id_seq
    INCREMENT 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1
    CACHE 1;

ALTER SEQUENCE public.user_id_seq
    OWNER TO postgres;

ALTER SEQUENCE public.request_id_seq
    OWNER TO postgres;

ALTER TABLE public.users ALTER COLUMN id SET DEFAULT NEXTVAL('user_id_seq'::regclass);
ALTER TABLE public.requests ALTER COLUMN id SET DEFAULT NEXTVAL('request_id_seq'::regclass);

ALTER TABLE IF EXISTS public.requests
    ADD CONSTRAINT ref_user_id FOREIGN KEY (owner_id)
    REFERENCES public.users (id) MATCH SIMPLE
    ON UPDATE CASCADE
       ON DELETE CASCADE
    NOT VALID;