public class test {
    public static void main(String[] args) {
        for (int i = 0; i < 16; i++) {
            System.out.println("create table t_link_goto_"+i +
                    "(\n" +
                    "    id             bigint auto_increment comment 'ID',\n" +
                    "    gid            varchar(32)  null comment '分组标识',\n" +
                    "    full_short_url varchar(128) null comment '完整短链接',\n" +
                    "    constraint t_link_goto_pk\n" +
                    "        primary key (id)\n" +
                    ");");
        }


        }

    }



