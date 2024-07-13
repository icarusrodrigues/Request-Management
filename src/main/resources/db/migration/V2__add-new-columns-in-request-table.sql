ALTER TABLE IF EXISTS public.requests
    ADD COLUMN request_status text NOT NULL;

ALTER TABLE IF EXISTS public.requests
    ADD COLUMN request_date timestamp NOT NULL;

ALTER TABLE IF EXISTS public.requests
    ADD COLUMN disapprove_reason text;