#!/usr/bin/env python
# -*- coding: utf-8 -*-

import qingcloud.iaas

conn = qingcloud.iaas.connect_to_zone('pek2','FZJUFGNWRAWENMUWXQDI','aZhjRwjcqgtSWtCoIAnmAos3KQXRfNzSM0N2Rpj8')
ret = conn.stop_hadoops(['hdp-n28i7len'])
print ret
