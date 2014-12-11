create or replace function T_1319_TICK_CNT return varchar2 is
  Result varchar2(12);
begin
  
 select min(num) into Result from
 (
   select min(a.f_start_num) num from t_1319_skillinfo a 
     where a.f_left_num > 0
   union all
   select b.f_skill_number num from t_1319_skill_number b 
     where b.f_oper_time = to_char(sysdate,'yyyymmddhh24mi')
 );
  return (Result);
exception
  when others then
    return (0);
end T_1319_TICK_CNT;
