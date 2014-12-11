create or replace procedure T_1319_SMS_ALERTS(MSG in varchar2) is
  Result number;
begin
  --张英剑
  INSERT INTO SMS_MT_SWAP (ID, DEST_TERMINAL_ID, SP_CODE, MSG_FORMAT, MSG_CONTENT, OPT_CODE)
    VALUES (SEQ_SWAP.NEXTVAL,'13851664447','10086',8, MSG,'JSYD');
  commit;
  Result := 1;
  --姜允林  
  INSERT INTO SMS_MT_SWAP (ID, DEST_TERMINAL_ID, SP_CODE, MSG_FORMAT, MSG_CONTENT, OPT_CODE)
    VALUES (SEQ_SWAP.NEXTVAL,'15996319248','10086',8, MSG,'JSYD');
  commit;
  Result := Result+1;
 
exception
  when others then
    rollback;
    dbms_output.put_line('sqlcode:' || sqlcode || ' ' || 'sqlerr:' || sqlerrm);
end T_1319_SMS_ALERTS;
