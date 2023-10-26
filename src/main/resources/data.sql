insert into event (`place_id`, `name`, `status`, `start_datetime`, `end_datetime`, `current_number_of_people`, `capacity`, `memo`, `created_at`, `updated_at`)
values
(1, '운동1', 'OPENED', '2021-01-01 09:00:00', '2021-01-01 12:00:00', 0, 20, 'test memo1', now() , now()),
(1, '운동2', 'OPENED', '2021-01-01 13:00:00', '2021-01-01 12:00:00', 0, 20, 'test memo2', now() ,now()),
(2, '행사1', 'OPENED', '2021-01-02 09:00:00', '2021-01-02 12:00:00', 0, 30, 'test memo3', now() ,now()),
(2, '행사2', 'OPENED', '2021-01-03 09:00:00', '2021-01-03 12:00:00', 0, 30, 'test memo4', now() ,now()),
(2, '행사3', 'CLOSED', '2021-01-04 09:00:00', '2021-01-04 12:00:00', 0, 30, 'test memo5', now() ,now()),
(2, '오전 스키', 'OPENED', '2021-02-01 08:00:00', '2021-02-01 12:30:00', 12, 50, 'test memo6', now(), now())
;