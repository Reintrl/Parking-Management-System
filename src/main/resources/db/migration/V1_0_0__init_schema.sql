create sequence public.parking_lots_id_seq;

alter sequence public.parking_lots_id_seq owner to asei;

create sequence public.parking_sessions_id_seq;

alter sequence public.parking_sessions_id_seq owner to asei;

create sequence public.reservations_id_seq;

alter sequence public.reservations_id_seq owner to asei;

create sequence public.spots_id_seq;

alter sequence public.spots_id_seq owner to asei;

create sequence public.tariffs_id_seq;

alter sequence public.tariffs_id_seq owner to asei;

create sequence public.users_id_seq;

alter sequence public.users_id_seq owner to asei;

create sequence public.vehicles_id_seq;

alter sequence public.vehicles_id_seq owner to asei;

create sequence public.security_id_seq;

alter sequence public.security_id_seq owner to asei;



create table if not exists public.tariffs
(
    status               varchar(255)                                       not null
    constraint tariff_status_check
    check ((status)::text = ANY
(ARRAY [('ACTIVE'::character varying)::text, ('INACTIVE'::character varying)::text])),
    billing_step_minutes integer                                            not null
    constraint tariffs_billing_step_minutes_check
    check (billing_step_minutes >= 1),
    free_minutes         integer                                            not null
    constraint tariffs_free_minutes_check
    check (free_minutes >= 0),
    hour_price           numeric(10, 2)                                     not null,
    id                   bigint default nextval('tariffs_id_seq'::regclass) not null
    primary key,
    name                 varchar(255)                                       not null
    unique,
    changed              timestamp(6)                                       not null,
    created              timestamp(6)                                       not null
    );

alter table public.tariffs
    owner to asei;

create table if not exists public.parking_lots
(
    id        bigint default nextval('parking_lots_id_seq'::regclass) not null
    primary key,
    tariff_id bigint                                                  not null
    constraint fk1yevy27rdk0x1g7cm4jy2tbp
    references public.tariffs,
    address   varchar(255)                                            not null
    unique,
    name      varchar(255)                                            not null,
    status    varchar(255)                                            not null
    constraint parking_lots_status_check
    check ((status)::text = ANY
((ARRAY ['ACTIVE'::character varying, 'CLOSED'::character varying, 'MAINTENANCE'::character varying])::text[])),
    changed   timestamp(6)                                            not null,
    created   timestamp(6)                                            not null
    );

alter table public.parking_lots
    owner to asei;

create table if not exists public.spots
(
    level          integer
    constraint spots_level_check
    check (level >= 0),
    number         integer                                          not null
    constraint spots_number_check
    check (number >= 1),
    id             bigint default nextval('spots_id_seq'::regclass) not null
    primary key,
    parking_lot_id bigint                                           not null
    constraint fk988d4mmltdx9x65jhv25qu1qm
    references public.parking_lots,
    status         varchar(255)                                     not null
    constraint spots_status_check
    check ((status)::text = ANY
((ARRAY ['AVAILABLE'::character varying, 'OCCUPIED'::character varying, 'OUT_OF_SERVICE'::character varying])::text[])),
    type           varchar(255)                                     not null
    constraint spots_type_check
    check ((type)::text = ANY
((ARRAY ['STANDARD'::character varying, 'ELECTRIC'::character varying, 'DISABLED'::character varying])::text[])),
    changed        timestamp(6)                                     not null,
    created        timestamp(6)                                     not null
    );

alter table public.spots
    owner to asei;

create unique index if not exists uk_spots_parking_lot_number
    on public.spots (parking_lot_id, number);

create index if not exists ix_spots_parking_lot_id
    on public.spots (parking_lot_id);

create table if not exists public.users
(
    disabled_permit boolean                                          not null,
    changed         timestamp(6)                                     not null,
    created         timestamp(6)                                     not null,
    id              bigint default nextval('users_id_seq'::regclass) not null
    primary key,
    first_name      varchar(255)                                     not null,
    second_name     varchar(255)                                     not null,
    email           varchar(255)                                     not null
    unique,
    status          varchar(255)                                     not null
    constraint users_status_check
    check ((status)::text = ANY ((ARRAY ['ACTIVE'::character varying, 'BLOCKED'::character varying])::text[]))
    );

alter table public.users
    owner to asei;

create table if not exists public.vehicles
(
    id           bigint default nextval('vehicles_id_seq'::regclass) not null
    primary key,
    user_id      bigint                                              not null
    constraint fko4u5y92lt2sx8y2dc1bb9sewc
    references public.users,
    plate_number varchar(15)                                         not null
    unique,
    type         varchar(255)                                        not null
    constraint vehicles_type_check
    check ((type)::text = ANY
((ARRAY ['CAR'::character varying, 'MOTORCYCLE'::character varying, 'TRUCK'::character varying, 'ELECTRIC_CAR'::character varying])::text[])),
    created      timestamp(6)                                        not null,
    changed      timestamp(6)                                        not null
    );

alter table public.vehicles
    owner to asei;

create table if not exists public.reservations
(
    end_time   timestamp(6)                                            not null,
    id         bigint default nextval('reservations_id_seq'::regclass) not null
    primary key,
    spot_id    bigint                                                  not null
    constraint fk4m0mjm7vrvfrfujmpo6n7323p
    references public.spots,
    start_time timestamp(6)                                            not null,
    vehicle_id bigint                                                  not null
    constraint fk7s1rlt0ccv2hjeaubf52di8er
    references public.vehicles,
    status     varchar(255)                                            not null
    constraint reservations_status_check
    check ((status)::text = ANY
((ARRAY ['ACTIVE'::character varying, 'CANCELLED'::character varying, 'EXPIRED'::character varying])::text[])),
    created    timestamp(6)                                            not null,
    changed    timestamp(6)                                            not null
    );

alter table public.reservations
    owner to asei;

create table if not exists public.parking_sessions
(
    end_time       timestamp(6),
    id             bigint default nextval('parking_sessions_id_seq'::regclass) not null
    primary key,
    spot_id        bigint                                                      not null
    constraint fkq7cdt09bwntgjbhwm7gsidcv1
    references public.spots,
    start_time     timestamp(6)                                                not null,
    vehicle_id     bigint                                                      not null
    constraint fkjpktyrfd2m3103vgike7xjao1
    references public.vehicles,
    status         varchar(255)                                                not null
    constraint parking_sessions_status_check
    check ((status)::text = ANY ((ARRAY ['ACTIVE'::character varying, 'FINISHED'::character varying])::text[])),
    reservation_id bigint
    constraint parking_sessions_reservations_id_fk
    references public.reservations
    constraint fkp9ssgejordr55ko4o9sm34miu
    references public.reservations,
    total_cost     numeric
    );

alter table public.parking_sessions
    owner to asei;

create index if not exists ix_sessions_spot_status
    on public.parking_sessions (spot_id, status);

create index if not exists ix_sessions_vehicle_status
    on public.parking_sessions (vehicle_id, status);

create table if not exists public.security
(
    id       bigint default nextval('security_id_seq'::regclass) not null
    primary key,
    username varchar(64)                                         not null
    unique,
    password varchar(255)                                        not null,
    role     varchar(32)                                         not null,
    user_id  bigint                                              not null
    unique
    references public.users
    );

alter table public.security
    owner to asei;

